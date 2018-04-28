#!/bin/bash

bash dev-tools/scripts/status-all.sh | grep -E "HEAD detached|Processing|Your branch|modified"
