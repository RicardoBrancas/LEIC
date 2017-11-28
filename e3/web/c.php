<?php
require 'php/connection.php';

$sql = 'SELECT ean FROM produto';

$result = $db->query($sql);

?>
<!DOCTYPE html>
<html>
<head>
<title>Alinea C</title>
</head>
<body>
  <h3>Listar eventos de reposicao</h3>
  <form action="php/treatreposition.php" method="POST">
    Escolha o produto: <br>
    <select name="ean">
      <?php
			while ($row = $result->fetch()) {
      echo "<option>$row[0]</option>\n";
      }
      ?>
    </select>
  </form>
</body>
</html>
