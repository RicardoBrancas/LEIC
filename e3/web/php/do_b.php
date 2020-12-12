<html>

<head>
	<title>Results - Alinea B</title>
</head>

<body>
<?php
try {
	require 'connection.php';
	$ean = $_POST["insert_ean"];
	$design = $_POST["insert_design"];
	$forn_prim = $_POST["insert_forn_prim"];
	$forn_sec = $_POST["insert_forn_sec"];
	$nif_forn_prim = $_POST["insert_nif_forn_prim"];
	$nome_forn_prim = $_POST["insert_nome_forn_prim"];
	$nif_forn_sec = $_POST["insert_nif_forn_sec"];
	$nome_forn_sec = $_POST["insert_nome_forn_sec"];
	$categoria = $_POST["insert_cat"];
	$remove_ean = $_POST["remove_ean"];

	$executed_sql = "";

	if (!empty($ean) and !empty($design) and !empty($forn_prim) and !empty($forn_sec) and !empty($categoria)) {
		$db->beginTransaction();

		$stmt = $db->prepare('INSERT INTO fornecedor VALUES (:nif, :nome)');

		if($forn_prim == 'new') {
			if(!empty($nif_forn_prim) and !empty($nome_forn_prim)) {
				$stmt->execute(['nif' => $nif_forn_prim, 'nome' => $nome_forn_prim]);
				$forn_prim = $nif_forn_prim;

				$executed_sql .= "INSERT INTO fornecedor VALUES ($nif_forn_prim, '$nome_forn_prim');\n";
			} else {
				echo "Erro: novo fornecedor primário inválido.";
				exit();
			}

		}

		if($forn_sec == 'new') {
			if(!empty($nif_forn_sec) and !empty($nome_forn_sec)) {
				$stmt->execute(['nif' => intval($nif_forn_sec), 'nome' => $nome_forn_sec]);
				$forn_sec = $nif_forn_sec;
				$executed_sql .= "INSERT INTO fornecedor VALUES ($nif_forn_sec, '$nome_forn_sec');\n";
			} else {
				echo "Erro: novo fornecedor secundário inválido.";
				exit();
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

	if(!empty($remove_ean)) {
		$stmt1 = $db->prepare('DELETE FROM reposicao WHERE ean = ?');
		$stmt2 = $db->prepare('DELETE FROM planograma WHERE ean = ?');
		$stmt3 = $db->prepare('DELETE FROM fornece_sec WHERE ean = ?');
		$stmt4 = $db->prepare('DELETE FROM produto WHERE ean = ?');

		$db->beginTransaction();
		$stmt1->execute([$remove_ean]);
		$stmt2->execute([$remove_ean]);
		$stmt3->execute([$remove_ean]);
		$stmt4->execute([$remove_ean]);
		$db->commit();

		echo "<pre>OK! ran:\nDELETE FROM reposicao WHERE ean = $remove_ean;\nDELETE FROM planograma WHERE ean = $remove_ean;\nDELETE FROM fornece_sec WHERE ean = $remove_ean;\nDELETE FROM produto WHERE ean = $remove_ean;</pre>";
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
