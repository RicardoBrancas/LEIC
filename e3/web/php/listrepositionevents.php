<?php
try {
	require 'connection.php';

	$ean = $_POST["ean"];

	if (is_numeric($ean) and strlen($ean) == 13) {
		$sql = "SELECT * FROM reposicao WHERE ean=?";
		$stmt = $db->prepare($sql);
		$stmt->execute([$ean]);
	}

	$db = null;
}
catch (PDOException $e) {
	echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>
