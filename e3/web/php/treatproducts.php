<?php
try {
    require 'connection.php';
    $ean = $_POST["ean"];
    $design = $_POST["design"];
    $nif_fornecedor_primario = $_POST["nif_fornecedor_primario"];
    $nome_fornecedor_primario = $_POST["nome_fornecedor_primario"];
    $nif_fornecedor_secundario = $_POST["nif_fornecedor_secundario"];
    $nome_fornecedor_secundario = $_POST["nome_fornecedor_secundario"];
    $categoria = $_POST["categoria"];
    $removeean = $_POST["removeean"];


    $db = new PDO("pgsql:host=$host;dbname=$dbname", $user, $password);
    $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    if ($ean != "" and $design != "" and $nif_fornecedor_primario != "" and $categoria != "" and $nome_fornecedor_primario != "" and $nif_fornecedor_secundario != "" and $nome_fornecedor_secundario != "") {
        $stmt = $db->prepare('INSERT INTO fornecedor VALUES (:nif, :nome)');
        $stmt1 = $db->prepare('INSERT INTO produto VALUES (:ean, :design, :categoria, :nif_fornecedor_primario, CURRENT_TIMESTAMP);');
        $stmt2 = $db->prepare('INSERT INTO fornece_sec VALUES (:nif, :ean);');
        $db->beginTransaction();
        $stmt->execute([$nif_fornecedor_primario, $nome_fornecedor_primario]);
        $stmt1->execute([$ean, $design, $categoria, $nif_fornecedor_primario]);
        $stmt->execute([$nome_fornecedor_secundario, $nome_fornecedor_secundario]);
        $stmt->execute([$nif_fornecedor_secundario, $nome_fornecedor_secundario]);
        $stmt->execute([$nif_fornecedor_secundario, $ean]);
        $db->commit();
    }
    
    $db = null;
} catch (PDOException $e) {
    echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>


