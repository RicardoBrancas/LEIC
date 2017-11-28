<?php

require 'connection.php';

$ean = $_POST['ean'];
$newdesign = $_POST['newdesign'];

if (is_numeric($ean) and strlen($ean) == 13)
	if ($newdesign != '') {
		$stmt = $db->prepare("UPDATE produto SET design=:des WHERE ean=:ean");
		$stmt->execute(['des' => $newdesign, 'ean' => $ean]);
		echo ('Designacao do produto ' + $ean + ' alterada para ' + $newdesign + ' com sucesso!');
	}
?>
