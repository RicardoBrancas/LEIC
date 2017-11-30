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
<form action="php/do_d.php" method="POST">
	Escolha o produto: <select name="insert_ean">
		<?php
		while ($row = $result->fetch()) {
			echo "<option>$row[0]</option>\n";
		}
		?>
	</select> <br>

	Insira a nova designacao para esse produto: <input type="text" name="insert_design"> <br>

	<input type="submit">
</form>

<a href="index.html">Index</a>

</body>
</html>
