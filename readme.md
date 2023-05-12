#Tezov project

You'll find here the source of the application Crypter + Lib.
https://play.google.com/store/apps/details?id=com.tezov.crypter.rse&hl=en&gl=US

All of this code are in Java. theses sources won't be updated anymore since right now I working to refactor most of it in Kotlin / Compose.


Even if I won't update them, I'll be glad to have any feedback and gladly answer to any request. I'll public share the new lib refactored in Kotlin and Compose.


Keep in ming, that's all of this was made for learning purpose. Everything is not a waste, but there are lot of improvment possible. Mainly about decoupling (i will not do it here, but you'll find it in Kotlin version)


Sadly, there is absolutly no documentation and absolutly no comment. But if interrested, I'll could make some sample and explain you how to use it. But mainly, you gonna have to explore yourself to understand


Crypter is not focus on the UI but on the functionnal part. Don't too hard :)

## What you will find ?
- Application single activity multi-fragment.
- Application in 3 modules + plugin userfriendly setup


## Module lib_java
Everything not link to android. These are 100% java and are fully fonctionnal on JEE project

What stuff interresting inside:
- cipher folder -> everything you need to user friendly create keys aes/rsa/..., cypher, decypher and so on
- async -> my own version of rxJava. More simple but very versatile and extensible.
- debug -> custom debug tool make it possible to even debug in release or obsfucated code with the help of my gradle plugin
- file -> every thing you need to user friendly manage files from API21 to API 31 with a single interface
- parser -> everything you need to user friendly parse XML/JSON/XLS and custom (plain or cypher) format with a single interface
- socker -> everything you need to send/receive packet over TCP/UDP server/client, auto beacon and so on
- buffer -> everything you need to work a byte/nible level with the easyness of C++
- All other are class to make all this functionnel

Sadly most of these tool are coupled.
 
 
 ## Module lib_java_android
 Everything link to android SDK but not link to the application
 
 What stuff interresting inside:
 - aidl -> simple functionnal code to share complexe data (file) between applications of your own installed on the same device
 - ads -> friendly class to use ads provider with a single interface
 - application -> everyhelper link to the application hardware / permission / connectivity / etc...
 - camera -> everything you need to use camera in app inside your own view
 - database -> my own DAO sqlite able to cypher on the fly and sync data from local / remote database
 - file -> complement to file package from lib_java to link with android system
 - provider -> every needed to use contentProvider / FileProvider and Services. If you data are too complexe, use AIDL
 - renderScript -> helper script to boost the camera image processing
 - ui -> a big pakcage to override all android view system and make it fonctionnel from API21 to last and never be stuck
   - in this, you find my own navigation / navigator implementation fully extensible
   - a big part focus and user data acquired and data presenter (not easy to use but very powerfull to make form)
   - and so on. I thing the ui part is very big and not easy to use. Look the app to understand.
 - type/runnable -> A very powerfull tool to build a coroutine flow like with the same functionality before the coroutine even exist.
 
##How to install
- Download all the folder (app_crypter / lib_java / lib_java_android / pluginTezov).
- Open the app_crypter folder with android studio
- sync and build.

