<?php
try {
	require 'connection.php';

	$catinsert = $_POST["categoryins"];
	$catremove = $_POST["categoryremove"];
	$subcatinsert = $_POST["categoryins1"];
	$supercat1 = $_POST["categoryins2"];
	$subcatremove = $_POST["subcategoryremove1"];
	$supercat2 = $_POST["subcategoryremove2"];

	if ($catinsert != "") {
		$sql = "INSERT INTO categoria VALUES ($catinsert);
            INSERT INTO categoria_simples VALUES($catinsert)"; //TODO
		$db->query($sql);
	}

	if ($catremove != "") {
		$sql = "DELETE FROM categoria WHERE nome=?";
		$stmt = $db->prepare($sql);
		$stmt->execute([$catremove]);
	}

	if ($subcatinsert != "" and $supercat1 != "") {
		$sql = "INSERT INTO constituida VALUES (:super, :sub)";
		$stmt = $db->prepare($sql);
		$stmt->execute(['super' => $supercat1, 'sub' => $subcatinsert]);
	}


	$db = null;
} catch (PDOException $e) {
	echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>
