<?php
try {
	require 'connection.php';

	$ean = $_POST["ean"];
	$newdesign = $_POST["newdesign"];

	if (is_numeric($ean) and strlen($ean) == 13)
		if ($newdesign != '') {
			$sql = "UPDATE produto SET design=:des WHERE ean=:ean";
			$stmt = $db->prepare($sql);
			$stmt->execute(['des' => $newdesign, 'ean' => 'ean']);
		}

	$db = null;
}
catch (PDOException $e) {
	echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>
