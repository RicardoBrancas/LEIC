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
<form action="php/do_c.php" method="GET">
	Escolha o produto: <select name="insert_ean">
		<?php
		while ($row = $result->fetch()) {
			echo "<option>$row[0]</option>\n";
		}
		?>
	</select> <br>
	<input type="submit" value="Submit">
</form>

<a href="index.html">Index</a>
</body>
</html>
