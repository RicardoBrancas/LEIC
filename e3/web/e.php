<?php
require 'php/connection.php';

$sql = 'SELECT nome FROM super_categoria';

$result = $db->query($sql);

?>
<!DOCTYPE html>
<html>
<head>
	<title>Alinea E</title>
</head>
<body>
<h3>Listar todas as sub-categorias de uma super-categoria</h3>
<form action="php/cat_children.php" method="GET">
	Escolha o nome da super-categoria <br>
	<select name="category">
		<?php
		while ($row = $result->fetch()) {
			echo "<option>$row[0]</option>\n";
		}
		?>
	</select>
	<input type="submit">
</form>

<a href="index.html">Index</a>

</body>
</html>