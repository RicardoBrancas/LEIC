<?php
require 'php/connection.php';

$sql = 'SELECT ean FROM produto';

$result = $db->query($sql);

$cats = $db->query($sql1);

?>
<!DOCTYPE html>
<html>
<head>
<title>Alinea B</title>
</head>
<body>
  <h3>Adicionar produto</h3>
  <form action="php/treatproducts.php" method="POST">
    EAN:<br>
    <input type="text" name="ean"> <br>
    Design:<br>
    <input type="text" name="design"> <br>
    NIF Fornecedor primario:<br>
    <input type="text" name="nif_fornecedor_primario"> <br>
    Nome Fornecedor primario:<br>
    <input type="text" name="nome_fornecedor_primario"> <br>
    NIF Fornecedor secundário:<br>
    <input type="text" name="nif_fornecedor_secundario"> <br>
    Nome Fornecedor secundário:<br>
    <input type="text" name="nome_fornecedor_secundario"> <br>
    Categoria:<br>
    <input type="text" name="categoria"> <br>
    <input type="submit" value="Submit">
  </form>
  <h3>Remover produto</h3>
  <form action="php/treatproducts.php" method="POST">
    Escreva o EAN do produto:<br>
    <select name="removeean">
        <?php
        while ($row = $cats->fetch()) {
            echo "<option>$row[0]</option>\n";
        }
        ?>
    </select> <br>
    <input type="submit" value="Submit">
  </form>

</body>
</html>
