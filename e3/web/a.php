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
<form action="php/treatcategories.php" method="POST">
	Escreva o nome da categoria: <br>
	<input type="text" name="simple_insert_cat"> <br>
	<input type="submit">
</form>

<h3>Inserir sub-categoria</h3>
<form action="php/treatcategories.php" method="POST">
	Escreva o nome da sub-categoria:<br>
	<input type="text" name="insert_sub_cat"> <br>
	Escolha a super-categoria:<br>
	<select name="insert_super_cat">
		<?php
		while ($row = $cats->fetch()) {
			echo "<option>$row[0]</option>\n";
		}
		?>
	</select> <br>
	<input type="submit" value="Submit">
</form>

<h3>Remover categoria</h3>
<form action="php/treatcategories.php" method="POST">
	Escreva o nome da categoria:<br>
	<select name="remove_cat">
		<?php
		while ($row = $simple_cats->fetch()) {
			echo "<option>$row[0]</option>\n";
		}
		?>
	</select> <br>
	<input type="submit">
</form>
<!--  <h3>Remover sub-categoria</h3>-->
<!--  <form action="php/treatcategories.php" method="POST">-->
<!--    Escreva o nome da sub-categoria:<br>-->
<!--    <input type="text" name="subcategoryremove1"> <br>-->
<!--    Escolha  super-categoria:<br>-->
<!--    <select name="category1">-->
<!--        --><?php
//        while ($row = $result1->fetch()) {
//            echo "<option>$row[0]</option>\n";
//        }
//        ?>
<!--    </select> <br>-->
<!--    <input type="submit" value="Submit">-->
<!--  </form>-->
</body>
</html>
