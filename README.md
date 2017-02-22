"# EscamQF001PtzControl" 

Speacial thanks to @tifred (https://www.dealabs.com/225375/tifred) from dealabs.com for this code 

"Salut à tous,

j'ai réussi à mettre la caméra sur le synology, en utilisant un rtsp proxy server, livre555proxyserver, une fois installé sur une machine linux qui fera office de server relais (192.168.1.18 par exemple) tuto ici : https://emtunc.org/blog/02/2016/setting-rtsp-relay-live555-proxy/

lancer en root : live555ProxyServer rtsp://IPCAM/user=admin_password=XXXX_channel=1_stream=0.sdp

ensuite apres quelques tentative sur synology pour l'intégrer ça a pu fonctionner avec les parametres user defined, ip 192.168.1.18 port : 8554, source : /prosyStream

je vois donc en live la caméra sur synology.

ensuite pour le PTZ,
j'ai analysé les trames réseau et je suis arrivé à les comprendre, j'ai réalisé un programme java pour envoyer les requêtes sur la caméra, (micro-server, requete http) et ça fonctionne, (j'ai mis le programme sur le server linux 192.168.1.18)
requete du type
http://192.168.1.18:4567/LoginCam/PasswordCam/IP/Direction/Step/Time


LoginCam : celui qui accede à la caméra
PasswordCam : celui à la caméra
IP : ip de la camera
Direction: direction du Pan ou Tilt (Left, Right,Up, Down, LeftUp, LeftDown, RightUp, RightDown)
Step : vitesse de rotation entre 1 et 8
Time : temps de rotation en milliseconde

je tiens les sources et l'executable à disposition par PM,
si quelqu'un veut le faire en python, ou autre langage de script

avec jeedom par contre c'est tout complètement intégré avec le plugin script qui envoie des requetes http, et le plugin surveillance station.

je n'arrive pas à intégrer l'option PTZ sur le synology meme
je suis preneur pour toutes intégrations complètes dans le syno

A plus
F."