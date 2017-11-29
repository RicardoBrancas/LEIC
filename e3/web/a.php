<?php
require 'php/connection.php';

$sql = 'SELECT nome FROM categoria';
$sql1 = 'SELECT nome FROM categoria_simples';

$cats = $db->query($sql);
$simple_cats = $db->query($sql);

?>
<!DOCTYPE html>
<html>
<head>
	<title>Alinea A</title>
</head>
<body>
<h3>Inserir categoria</h3>
<form action="php/do_a.php" method="POST">
	Escreva o nome da categoria: <input type="text" name="simple_insert_cat"> <br>
	<input type="submit">
</form>

<h3>Inserir sub-categoria</h3>
<form action="php/do_a.php" method="POST">
	Escreva o nome da sub-categoria: <input type="text" name="insert_sub_cat"> <br>
	Escolha a super-categoria: <select name="insert_super_cat">
		<?php
		while ($row = $cats->fetch()) {
			echo "<option>$row[0]</option>\n";
		}
		?>
	</select> <br>
	<input type="submit" value="Submit">
</form>

<h3>Remover categoria</h3>
<form action="php/do_a.php" method="POST">
	Escreva o nome da categoria: <select name="remove_cat">
		<?php
		while ($row = $simple_cats->fetch()) {
			echo "<option>$row[0]</option>\n";
		}
		?>
	</select> <br>
	<input type="submit">
</form>

<a href="index.html">Index</a>
</body>
</html>
