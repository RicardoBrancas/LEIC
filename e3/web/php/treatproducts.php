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


	if (!empty($ean) and !empty($design) and !empty($forn_prim) and !empty($forn_sec) and !empty($categoria)) {
		$db->beginTransaction();

		$stmt = $db->prepare('INSERT INTO fornecedor VALUES (:nif, :nome)');

		if($forn_prim == 'new') {
			if(!empty($nif_fornecedor_primario) and !empty($nome_fornecedor_primario)) {
				$stmt->execute(['nif' => intval($nif_fornecedor_primario), 'nome' => $nome_fornecedor_primario]);
				$forn_prim = $nif_fornecedor_primario;
			} else {
				//TODO
			}

		}

		if($forn_sec == 'new') {
			if(!empty($nif_fornecedor_secundario) and !empty($nome_fornecedor_secundario)) {
				$stmt->execute(['nif' => intval($nif_fornecedor_secundario), 'nome' => $nome_fornecedor_secundario]);
				$forn_sec = $nif_fornecedor_secundario;
			} else {
				//TODO
			}
		}

		$stmt1 = $db->prepare('INSERT INTO produto VALUES (:ean, :design, :categoria, :nif_fornecedor_primario, CURRENT_TIMESTAMP);');
		$stmt2 = $db->prepare('INSERT INTO fornece_sec VALUES (:nif, :ean);');
		$stmt1->bindParam(":ean", $ean, PDO::PARAM_INT);
		$stmt1->bindParam(":nif_fornecedor_primario", $forn_prim, PDO::PARAM_INT);
		$stmt1->bindParam(':design', $design);
		$stmt1->bindParam(':categoria', $categoria);
		$stmt1->execute();
		$stmt2->bindParam(':nif', $forn_sec, PDO::PARAM_INT);
		$stmt2->bindParam(':ean', $ean, PDO::PARAM_INT);
		$stmt2->execute();
		$db->commit();
	}

	if(!empty($removeean)) {
		$stmt1 = $db->prepare('DELETE FROM fornece_sec WHERE ean = ?');
		$stmt2 = $db->prepare('DELETE FROM produto WHERE ean = ?');

		$db->beginTransaction();
		$stmt1->execute([intval($removeean)]);
		$stmt2->execute([intval($removeean)]);
		$db->commit();
	}

	$db = null;
} catch (PDOException $e) {
	echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>


