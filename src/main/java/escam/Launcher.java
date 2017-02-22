package escam;

import static spark.Spark.get;

public class Launcher {

	/**
	 * lancer en root : live555ProxyServer
	 * rtsp://IPCAM/user=admin_password=XXXX_channel=1_stream=0.sdp
	 * 
	 * ensuite apres quelques tentative sur synology pour l'intégrer ça a pu
	 * fonctionner avec les parametres user defined, ip 192.168.1.18 port :
	 * 8554, source : /prosyStream
	 * 
	 * je vois donc en live la caméra sur synology.
	 * 
	 * ensuite pour le PTZ, j'ai analysé les trames réseau et je suis arrivé à
	 * les comprendre, j'ai réalisé un programme java pour envoyer les requêtes
	 * sur la caméra, (micro-server, requete http) et ça fonctionne, (j'ai mis
	 * le programme sur le server linux 192.168.1.18) requete du type
	 * http://192.168.1.18:4567/LoginCam/PasswordCam/IP/Direction/Step/Time
	 * 
	 * 
	 * LoginCam : celui qui accede à la caméra PasswordCam : celui à la caméra
	 * IP : ip de la camera Direction: direction du Pan ou Tilt (Left, Right,Up,
	 * Down, LeftUp, LeftDown, RightUp, RightDown) Step : vitesse de rotation
	 * entre 1 et 8 Time : temps de rotation en milliseconde
	 */

	public static void main(String[] args) {
		get("/ptz/:login/:password/:ip/:direction/:step/:time", (req, res) -> {
			System.out.println(req.params());

			String ipS = req.params(":ip");
			String login = req.params(":login");
			String password = req.params(":password");
			String[] ip = ipS.split("\\.");

			int time = Integer.parseInt(req.params(":time"));
			String stepNumber = req.params(":step");
			String direction = req.params(":direction");
			Step step = Step.valueOf("Step" + stepNumber);
			Direction dir = Direction.valueOf("Direction" + direction);

			EscamQF001 cam = new EscamQF001(Integer.parseInt(ip[0]), Integer.parseInt(ip[1]), Integer.parseInt(ip[2]),
					Integer.parseInt(ip[3]), 34567);
			cam.connect(login, password);
			//cam.keepAlive();
			cam.startPTZ(); //
			cam.actionDirection(step, dir, time);
			cam.stopPTZ();
			return "Hello World";
		});
	}

}
