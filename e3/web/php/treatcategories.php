<?php
try
 {
 $host = "db.ist.utl.pt";
 $user ="ist424836";
 $password = "xxxxxxxx";
 $dbname = $user;
 $catinsert = $_POST["categoryins"];
 $catremove= $_POST["categoryremove"];
 $subcatinsert = $_POST["categoryins1"];
 $supercat1 = $_POST["categoryins2"];
 $subcatremove = $_POST["subcategoryremove1"];
 $supercat2 = $_POST["subcategoryremove2"];


 $db = new PDO("pgsql:host=$host;dbname=$dbname", $user, $password);
 $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

 if ($catinsert != ""){
   $sql = "INSERT INTO categoria VALUES ($catinsert);
            INSERT INTO categoria_simles VALUES($catinsert)"; //TODO
   $db->query($sql);
 }

 if ($catremove != ""){
   $sql = "DELETE FROM categoria WHERE nome=$catremove;";
   $db->query($sql);
 }

 if ($subcatinsert != "" and $supercat1 != ""){
   $sql = "INSERT INTO constituida VALUES ($supercat1, $subcatinsert)";
   $db->query($sql);
 }


 $db = null;
 }
 catch (PDOException $e)
 {
 echo("<p>ERROR: {$e->getMessage()}</p>");
 }
?>
