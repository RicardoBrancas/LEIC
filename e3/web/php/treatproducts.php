<html>

<head>
	<title>Results - Alinea B</title>
</head>

<body>
<?php
try {
	require 'connection.php';
	$ean = $_POST["ean"];
	$design = $_POST["design"];
	$forn_prim = $_POST["forn_prim"];
	$forn_sec = $_POST["forn_sec"];
	$nif_fornecedor_primario = $_POST["nif_fornecedor_primario"];
	$nome_fornecedor_primario = $_POST["nome_fornecedor_primario"];
	$nif_fornecedor_secundario = $_POST["nif_fornecedor_secundario"];
	$nome_fornecedor_secundario = $_POST["nome_fornecedor_secundario"];
	$categoria = $_POST["categoria"];
	$removeean = $_POST["removeean"];

	$executed_sql = "";

	if (!empty($ean) and !empty($design) and !empty($forn_prim) and !empty($forn_sec) and !empty($categoria)) {
		$db->beginTransaction();

		$stmt = $db->prepare('INSERT INTO fornecedor VALUES (:nif, :nome)');

		if($forn_prim == 'new') {
			if(!empty($nif_fornecedor_primario) and !empty($nome_fornecedor_primario)) {
				$stmt->execute(['nif' => $nif_fornecedor_primario, 'nome' => $nome_fornecedor_primario]);
				$forn_prim = $nif_fornecedor_primario;

				$executed_sql .= "INSERT INTO fornecedor VALUES ($nif_fornecedor_primario, '$nome_fornecedor_primario');\n";
			} else {
				//TODO
			}

		}

		if($forn_sec == 'new') {
			if(!empty($nif_fornecedor_secundario) and !empty($nome_fornecedor_secundario)) {
				$stmt->execute(['nif' => intval($nif_fornecedor_secundario), 'nome' => $nome_fornecedor_secundario]);
				$forn_sec = $nif_fornecedor_secundario;
				$executed_sql .= "INSERT INTO fornecedor VALUES ($nif_fornecedor_secundario, '$nome_fornecedor_secundario');\n";
			} else {
				//TODO
			}
		}

		$stmt3 = $db->prepare('INSERT INTO produto VALUES (:ean, :design, :categoria, :nif_fornecedor_primario, CURRENT_TIMESTAMP);');
		$stmt4 = $db->prepare('INSERT INTO fornece_sec VALUES (:nif, :ean);');
		$stmt3->execute(['ean' => $ean, 'nif_fornecedor_primario' => $forn_prim, 'design' => $design, 'categoria' => $categoria]);
		$stmt4->execute(['nif' => $forn_sec, 'ean' => $ean]);
		$db->commit();

		$executed_sql .= "INSERT INTO produto VALUES ($ean, '$design', '$categoria', $forn_prim, CURRENT_TIMESTAMP);\n";
		$executed_sql .= "INSERT INTO fornece_sec VALUES ($forn_sec, $ean);\n";

		echo "<pre>OK! ran:\n$executed_sql</pre>";
	}

	if(!empty($removeean)) {
		$stmt1 = $db->prepare('DELETE FROM reposicao WHERE ean = ?');
		$stmt2 = $db->prepare('DELETE FROM planograma WHERE ean = ?');
		$stmt3 = $db->prepare('DELETE FROM fornece_sec WHERE ean = ?');
		$stmt4 = $db->prepare('DELETE FROM produto WHERE ean = ?');

		$db->beginTransaction();
		$stmt1->execute([$removeean]);
		$stmt2->execute([$removeean]);
		$stmt3->execute([$removeean]);
		$stmt4->execute([$removeean]);
		$db->commit();

		echo "<pre>OK! ran:\nDELETE FROM reposicao WHERE ean = $removeean;\nDELETE FROM planograma WHERE ean = $removeean;\nDELETE FROM fornece_sec WHERE ean = $removeean;\nDELETE FROM produto WHERE ean = $removeean;</pre>";
	}

	$db = null;
} catch (PDOException $e) {
	try {
		$db->rollBack();
	} catch (Exception $e) {}

	echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>

<a href="../b.php">Back</a>

</body>
</html>


