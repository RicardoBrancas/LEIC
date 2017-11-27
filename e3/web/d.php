<?php
require 'php/connection.php';

$sql = 'SELECT ean FROM produto';

$result = $db->query($sql);

?>
<!DOCTYPE html>
<html>
<head>
  <title>Alinea D</title>
</head>
<body>
<h3>Alterar designacao do produto</h3>
<form action="php/treatdesign.php" method="POST">
	Escolha o produto: <br>
	<select name="ean">
		<?php
			while ($row = $result->fetch()) {
				echo "<option>$row[0]</option>\n";
		}
		?>
	</select>
	Insira a nova designacao para esse produto: <br>
	<input type="text" name="novadesign"> <br>
	<input type="submit" value="Submit">
</form>
</body>
</html>