<?php
try {
	require 'connection.php';

	$cat_insert = $_POST["simple_insert_cat"];
	$cat_remove = $_POST["remove_cat"];
	$sub_cat_insert = $_POST["insert_sub_cat"];
	$super_cat_insert = $_POST["insert_super_cat"];

	if (!empty($cat_insert)) {
		$stmt1 = $db->prepare('INSERT INTO categoria VALUES (?);');
		$stmt2 = $db->prepare('INSERT INTO categoria_simples VALUES(?);');

		$db->beginTransaction();
		$stmt1->execute([$cat_insert]);
		$stmt2->execute([$cat_insert]);
		$db->commit();
	}

	if (!empty($cat_remove)) {
		$stmt = $db->prepare("SELECT remove_cat(?);");

		$db->beginTransaction();
		$stmt->execute([$cat_remove]);
		$db->commit();
	}

	if (!empty($sub_cat_insert) and !empty($super_cat_insert)) {
		$stmt = $db->prepare("SELECT sub_cat_insert(:super, :sub);");
		$db->beginTransaction();
		$stmt->execute(['super' => $super_cat_insert, 'sub' => $sub_cat_insert]);
		$db->commit();
	}


	$db = null;
} catch (PDOException $e) {
	echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>
