<?php
require 'php/connection.php';

$sql = 'SELECT nome FROM super_categoria';
$sql1 = 'SELECT nome FROM categoria';

$result = $db->query($sql);
$result1 = $db->query($sql);

$cats = $db->query($sql1);

?>
<!DOCTYPE html>
<html>
<head>
<title>Alinea A</title>
</head>
<body>
  <h3>Inserir categoria</h3>
  <form action="php/treatcategories.php" method="POST">
    Escreva o nome da categoria: <br>
    <input type="text" name="categoryins"> <br>
    <input type="submit" value="Submit">
  </form>
  <h3>Remover categoria</h3>
  <form action="php/treatcategories.php" method="POST">
    Escreva o nome da categoria:<br>
    <select name="categoryremove">
        <?php
        while ($row = $cats->fetch()) {
            echo "<option>$row[0]</option>\n";
        }
        ?>
    </select> <br>
    <input type="submit" value="Submit">
  </form>
  <h3>Inserir sub-categoria</h3>
  <form action="php/treatcategories.php" method="POST">
    Escreva o nome da sub-categoria:<br>
    <input type="text" name="categoryins1"> <br>
    Escolha a super-categoria:<br>
    <select name="category">
        <?php
        while ($row = $result->fetch()) {
            echo "<option>$row[0]</option>\n";
        }
        ?>
    </select> <br>
    <input type="submit" value="Submit">
  </form>
  <h3>Remover sub-categoria</h3>
  <form action="php/treatcategories.php" method="POST">
    Escreva o nome da sub-categoria:<br>
    <input type="text" name="subcategoryremove1"> <br>
    Escolha  super-categoria:<br>
    <select name="category1">
        <?php
        while ($row = $result1->fetch()) {
            echo "<option>$row[0]</option>\n";
        }
        ?>
    </select> <br>
    <input type="submit" value="Submit">
  </form>
</body>
</html>
