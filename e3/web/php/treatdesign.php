<?php

require 'connection.php';

$ean = $_POST['ean'];
$des = $_POST['newdesign'];

if ($des != '') {
	$stmt = $db->prepare("UPDATE produto SET design=:des WHERE ean=:ean");
	$stmt->execute(['des' => $des, 'ean' => $ean]);
	echo "Designacao do produto $ean alterada para $des com sucesso!";
}
else {
	echo 'A nova designacao nao e valida.';
}
?>
