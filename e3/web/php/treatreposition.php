<?php
try {
	require 'connection.php';

	$ean = $_POST['ean'];

	if (is_numeric($ean) and strlen($ean) == 13) {
		$stmt = $db->prepare("SELECT (:table) FROM reposicao WHERE ean=:ean");
		$db->beginTransaction();
		$stmt->execute(['table' => $table,'ean' => $ean]);
		$db->commit();
	}

	$db = null;
}
catch (PDOException $e) {
	echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>
