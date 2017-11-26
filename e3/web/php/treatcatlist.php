<?php
try {
	require 'connection.php';

	$category = $_POST['category'];

	if ($category != '') {
		$stmt = $db->prepare();
		$db->beginTransaction();
		$stmt->execute();
		$db->commit();
		}

	$db = null;
}
catch (PDOException $e) {
	echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>
