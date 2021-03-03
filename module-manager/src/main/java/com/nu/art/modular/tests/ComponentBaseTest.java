package com.nu.art.modular.tests;

import com.nu.art.belog.Logger;
import com.nu.art.core.generics.Processor;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ComponentBaseTest
	extends Logger {

	@SuppressWarnings("unchecked")
	public abstract class BaseTest<T extends BaseTest> {

		protected String name;
		protected String description;
		protected Throwable t;

		public String getName() {
			return name;
		}

		public T setName(String name) {
			this.name = name;
			return (T) this;
		}

		public T setDescription(String description) {
			this.description = description;
			return (T) this;
		}

		public Throwable getException() {
			return t;
		}

		abstract void execute();

		abstract boolean validate();
	}

	public class Scenario
		extends BaseTest<Scenario> {

		private ArrayList<BaseTest> tests = new ArrayList<>();

		public Scenario addTest(BaseTest test) {
			if (test instanceof TestItem)
				((TestItem) test).setTimeout(-1);
			tests.add(test);
			return this;
		}

		@Override
		public final void execute() {
			for (BaseTest test : tests) {
				test.execute();
				test.validate();
			}
		}

		@Override
		boolean validate() {
			return true;
		}
	}

	public class AsyncScenario
		extends BaseTest<AsyncScenario> {

		private ArrayList<BaseTest> tests = new ArrayList<>();

		public AsyncScenario addTest(BaseTest test) {
			tests.add(test);
			return this;
		}

		public void execute() {
			final AtomicInteger counter = new AtomicInteger();
			for (final BaseTest test : tests) {
				counter.incrementAndGet();
				new Thread(new Runnable() {
					@Override
					public void run() {
						test.execute();
						counter.decrementAndGet();
						synchronized (counter) {
							logDebug("Test is finished: " + test.name);
							counter.notify();
						}
					}
				}, "test--" + test.name).start();
			}

			while (counter.get() > 0) {
				try {
					synchronized (counter) {
						counter.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			validate();
		}

		@Override
		protected boolean validate() {
			BaseTest[] failedTests = getFailedTests();
			if (failedTests.length == 0)
				return true;

			//			try {
			//				Thread.sleep(500);
			//			} catch (InterruptedException e) {
			//				e.printStackTrace();
			//			}

			logError("Error in test " + name);
			for (BaseTest failedTest : failedTests) {
				logError(" - Action name: " + failedTest.getName());
				logError(failedTest.getException());
				logError("            ----------------------------------------------------             ");
			}

			throw new AsyncTestException();
		}

		@SuppressWarnings( {
			                   "unchecked",
			                   "SuspiciousToArrayCall"
		                   })
		private BaseTest[] getFailedTests() {
			ArrayList<BaseTest> failedTests = new ArrayList<>();
			for (final BaseTest test : tests) {
				if (!test.validate())
					failedTests.add(test);
			}
			return failedTests.toArray(new TestItem[0]);
		}
	}

	public interface TestValidator<ResultType> {

		boolean validate(ResultType result, Throwable t);
	}

	protected final class TestItem<T>
		extends BaseTest<TestItem<T>> {

		private final AtomicReference<T> ref = new AtomicReference<>();
		private Processor<TestItem<T>> processor;
		private TestValidator<T> validator;
		private T expectedValue;
		private int timeout = 10000;

		public TestItem() { }

		public TestItem<T> setTimeout(int timeout) {
			this.timeout = timeout;
			return this;
		}

		public TestItem<T> setProcessor(Processor<TestItem<T>> processor) {
			this.processor = processor;
			return this;
		}

		public TestItem<T> setValidator(TestValidator<T> validator) {
			this.validator = validator;
			return this;
		}

		public TestItem<T> setValidator(final boolean expectedSuccess) {
			this.validator = new TestValidator<T>() {
				@Override
				public boolean validate(T result, Throwable t) {
					return expectedSuccess == (t == null);
				}
			};
			return this;
		}

		private synchronized void _notify() {
			this.notify();
		}

		public final synchronized void _set(T value) {
			logDebug("Setting result: " + value);

			ref.set(value);
			_notify();
		}

		private synchronized void _wait(int timeout) {
			logInfo("Waiting: " + timeout + "ms");

			try {
				this.wait(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private synchronized void _wait() {
			logInfo("Waiting...");

			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public final synchronized void _set(Throwable t) {
			logError("Setting error: ", t);

			this.t = t;
			_notify();
		}

		final boolean validate() {
			T result = ref.get();
			if (t == null)
				if (result == null)
					this.t = new RuntimeException("Did not receive result");
				else if (!result.equals(expectedValue))
					this.t = new RuntimeException("Did not receive expected value:\n  Expected: " + expectedValue + "\n  Found: " + result);

			return validator.validate(result, this.t);
		}

		final void execute() {
			logInfo("Running  test: " + description);
			processor.process(this);
			if (timeout > 0)
				_wait(timeout);
		}

		public TestItem<T> expectedValue(T expectedValue) {
			this.expectedValue = expectedValue;
			return this;
		}
	}

	protected final TestItem<Boolean> createTest(String name, String description) {
		return new TestItem<Boolean>()
			.setName(name)
			.setDescription(description)
			.expectedValue(true)
			.setValidator(true);
	}

	protected final AsyncScenario createAsyncScenario() {
		return createAsyncScenario(Thread.currentThread().getStackTrace()[2].getMethodName());
	}

	protected final AsyncScenario createAsyncScenario(String name) {
		return new AsyncScenario().setName(name);
	}

	protected final Scenario createScenario() {
		return createScenario(Thread.currentThread().getStackTrace()[2].getMethodName());
	}

	protected final Scenario createScenario(String name) {
		return new Scenario().setName(name);
	}
}
