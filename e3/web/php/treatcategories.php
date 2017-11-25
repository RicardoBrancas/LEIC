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
		$stmt1 = $db->prepare('INSERT INTO categoria VALUES (?);');
		$stmt2 = $db->prepare('INSERT INTO categoria_simples VALUES(?);');

		$db->beginTransaction();
		$stmt1->execute([$catinsert]);
		$stmt2->execute([$catinsert]);
		$db->commit();
	}

	if ($catremove != "") {
		$sql = "DELETE FROM categoria WHERE nome=?;";
		$stmt = $db->prepare($sql);
		$stmt->execute([$catremove]);
	}

	if ($subcatinsert != "" and $supercat1 != "") {
		$sql = "INSERT INTO constituida VALUES (:super, :sub);";
		$stmt = $db->prepare($sql);
		$stmt->execute(['super' => $supercat1, 'sub' => $subcatinsert]);
	}


	$db = null;
} catch (PDOException $e) {
	echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>
