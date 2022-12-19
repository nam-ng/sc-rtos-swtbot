# sc-rtos-swtbot
Automated test for SC RTOS using SWTBot
First stage is to update SWTBot script to check build for Azure RTOS package.

Further one will migrate the automated test of SC RTOS from RCPTT to SWTBot.

# Update on 19/12/2022:
Support ewf and usbx mass storage PG and build

Refactor the code to avoid duplication by using TestUtils.java, which is a utility java class