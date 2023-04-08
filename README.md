# hsc-deamon

Daemon for reading and sending data generated  by Hunt 

Daemon need Java to run. If you do not have Java it should open prompt to install it. However, I'm not 100% sure since I did not test running it without Java

To run, execute:
```
hsc-daemon.exe
```
Daemon needs two parameters: hunt attributes path and player name used in match.

Usually, those are extracted from Windows steam registry but in some cases registries cannot be found from whatever reason,
so it is possible to override default setting:
```
hsc-daemon.exe --hunt-attributes-path <path to attributes in Hunt folder> --player-name <Steam name used in match>
```
After start all logs are stored in hscDaemon.log file where you can check if daemon works 
properly. The file is located in the same directory as program.

