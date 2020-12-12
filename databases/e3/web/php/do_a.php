<html>

<head>
	<title>Results - Alinea A</title>
</head>

<body>
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

		echo "<pre>OK! ran:\nINSERT INTO categoria VALUES ('$cat_insert');\nINSERT INTO categoria_simples VALUES('$cat_insert');</pre>";
	}

	if (!empty($cat_remove)) {
		$stmt = $db->prepare("SELECT remove_cat(?);");

		$stmt->execute([$cat_remove]);

		echo "<pre>OK! ran:\nSELECT remove_cat('$cat_remove');</pre>";
	}

	if (!empty($sub_cat_insert) and !empty($super_cat_insert)) {
		$stmt = $db->prepare("SELECT sub_cat_insert(:super, :sub);");

		$stmt->execute(['super' => $super_cat_insert, 'sub' => $sub_cat_insert]);

		echo "<pre>OK! ran:\nSELECT sub_cat_insert('$super_cat_insert', '$sub_cat_insert');</pre>";
	}


	$db = null;
} catch (PDOException $e) {
	try {
		$db->rollBack();
	} catch (Exception $e) {}

	echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>

<a href="../a.php">Back</a>

</body>
</html>