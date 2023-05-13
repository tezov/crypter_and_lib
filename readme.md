# Tezov project (Old Java)

## English introduction
You'll find here the sources of the application Crypter + Lib.


https://play.google.com/store/apps/details?id=com.tezov.crypter.rse&hl=en&gl=US


All of thes sources are in Java. It won't be updated anymore since right now I working to refactor most of it in Kotlin / Compose (with a full decoupling and depency injection throw multi-modules too)


Even if I won't update them, I'll be glad to have any feedback and gladly answer to any request. I'll public share the new lib refactored in Kotlin and Compose soon.


Keep in mind, that's all of this was made for learning purpose but still in very robust way. Everything is not a waste, but there are lot of improvment possible. Mainly about decoupling that's make all this code impossible to pass through testing.


Sadly, there is absolutly no documentation and absolutly no comment. But if interrested, I'll could make some sample and explain you how to use it. Mainly, you gonna have to explore yourself to understand.


Crypter is not focus on the UI but on the functionnal part. Don't be too harsh :)


## French introduction
J'ouvre en public le dépot d'une application 100% Java avec toutes les libraries incluses.

Je ne les maintiens plus aujourd'hui puisque refactor en full kotlin / compose que je metterai aussi en public.

Y'a encore du bon dedans malgré le couplage important des classes.

les choses interessantes et trés faciliement portable en kotlin:

- le package cipher : framework userfriendly sur le cryptage. Vous y trouverez tous ce qu'il faut pour crypter avec n'importe quelle algo avec la même interface. Tous les adapters étant fourni.
- le package socket : tous ce qu'il faut pour transmettre en TCP/ UDP client/server avec détection automatique et gestion de beacon de configuration
- le package type/runnable : les couroutines en Java avec l'heure.
- le package async : ma solution RxJava multi-thread
- le package database : mon propre DAO avec gestion de synchro local/remote. Trés ancienne, donc je dirai qu'il y a beaucoup de mauvais choix technique sur cette partie. Mais toujours sympas pour apprendre.
- Tous le package UI qui contient les wrapper des quasiments toutes les vue Android permettant entre autre d'être 100% robuste au évolution API. (Beaucoup de mauvais choix ici, mais trés fonctionnel
- Dans le package UI, vous traverez aussi ma propre implementation Navigation. A ce demander si Google ne m'a pas copié en douce :). Permettant d'enregistrer son graph de navigation Activité/Fragment/Dialog avec gestion d'animation et transfert de donnée.
- Dans le package Application, vous trouverez plein d'outils utils pour s'affranchir des difficultés du SDK d'android concernant la connectivity, les permissions, la déctection d'ouverture claver et poussage de vue de l'API21 jusqu'au 31.
- Dans le package file, vous trouverez un truc absolument fabuleux. La gestion de fichiers sur la méme interface de l'API21 à 31 et avec génération de liens fichiers indépendant de la plateform. Permettant nottement l'enregistrement du path de fichier en étant insensible à la plateform.
- Et plein de truc encore dont je me souviens plus.


## What you will find ?
- Single activity multi-fragment.
- 3 modules custom architecture + plugin userfriendly setup
- 100% reactive through my ownRxJava like which I call Notifier.

## Module lib_java
Everything not link to android. These are 100% java and are fully fonctionnal on JEE project

What stuff interresting inside:
- cipher folder -> everything you need to user friendly create keys aes/rsa/..., cypher, decypher and so on through a single interface for all possible encryption Android support and any type adpater.
- async -> my own version of rxJava. More simple but very versatile and extensible.
- debug -> custom debug tool make it possible to even debug in release or obsfucated code.
- file -> every thing you need to user friendly manage files from API21 to API31 with a single interface and manage link in independant platform way.
- parser -> everything you need to user friendly parse XML/JSON/XLS and custom (plain or cypher) format with a single interface
- socker -> everything you need to send/receive packet over TCP/UDP server/client, auto beacon and so on
- buffer -> everything you need to work a byte/nible level with the easyness of C++
- type/runnable -> A very powerfull tool to build a coroutine flow like with the same functionality before the coroutine even exist.
- All other are class to make all this functionnal

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
 
  ## Module app_crypter
  The crypter application using these 2 modules.
  
  ## plugin TezovApp
  Gradle plugin is groovy to mange the release / debug clean and add a user friendly versionning gestion.
 
## How to install
- Download all the folder (app_crypter / lib_java / lib_java_android / pluginTezov).
- Open the app_crypter folder with android studio
- sync and build.

