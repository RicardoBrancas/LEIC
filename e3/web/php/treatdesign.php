<?php
try {
	$host = "db.ist.utl.pt";
	$user = "ist424836";
	$password = "xxxxxxxx";
	$dbname = $user;
	$ean = $_POST["ean"];
	$newdesign = $_POST["newdesign"];

	$db = new PDO("pgsql:host=$host;dbname=$dbname", $user, $password);
	$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

	if (is_numeric($ean) and strlen($ean) == 13)
		if ($newdesign != '') {
			$sql = "UPDATE produto SET designacao=$newdesign WHERE ean=$ean";
			$db->query($sql);
		}

	$db = null;
}
catch (PDOException $e) {
	echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>
