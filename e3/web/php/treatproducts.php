<?php
try
 {
 $host = "db.ist.utl.pt";
 $user ="ist424836";
 $password = "xxxxxxxx";
 $dbname = $user;
 $ean = $_POST["ean"];
 $design= $_POST["design"];
 $nif_fornecedor_primario = $_POST["nif_fornecedor_primario"];
 $nome_fornecedor_primario = $_POST["nome_fornecedor_primario"];
 $categoria = $_POST["categoria"];
 $removeean = $_POST["removeean"];


 $db = new PDO("pgsql:host=$host;dbname=$dbname", $user, $password);
 $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

 if ($ean != "" and $design !="" and $fornecedor_primario != "" and $categoria != ""){
  $sql = "INSERT INTO produto VALUES ($ean, $design, $categoria, $nif_fornecedor_primario, CURRENT_TIMESTAMP);
           INSERT INTO fornecedor VALUES ($nif_fornecedor_primario, $nome_fornecedor_primario)";
  $db->query($sql);
 }


 $db = null;
 }
 catch (PDOException $e)
 {
 echo("<p>ERROR: {$e->getMessage()}</p>");
 }
?>
