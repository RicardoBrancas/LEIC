<?php
try {
	require 'connection.php';

	$ean = $_POST['ean'];
	$newdesign = $_POST['newdesign'];

	if (is_numeric($ean) and strlen($ean) == 13)
		if ($newdesign != '') {
			$stmt = $db->prepare("UPDATE produto SET design=:des WHERE ean=:ean");
			$db->beginTransaction();
			$stmt->execute(['des' => $newdesign, 'ean' => $ean]);
			$db->commit();
		}a

	$db = null;
}
catch (PDOException $e) {
	echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>
