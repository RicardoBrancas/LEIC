<?php
try {
	require 'connection.php';

	$category = $_POST['category'];

	if ($category != '') {
		$stmt = $db->prepare("UPDATE produto SET design=:des WHERE ean=:ean");
		$db->beginTransaction();
		$stmt->execute(['des' => $newdesign, 'ean' => $ean]);
		$db->commit();
		}

	$db = null;
}
catch (PDOException $e) {
	echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>
