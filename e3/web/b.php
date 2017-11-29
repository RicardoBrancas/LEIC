<?php
require 'php/connection.php';

$cats = $db->query('SELECT nome FROM categoria;');
$forns = $db->query('SELECT nif FROM fornecedor;')->fetchAll();

$eans = $db->query('SELECT ean FROM produto;');
?>
<!DOCTYPE html>
<html>
<head>
	<title>Alinea B</title>
</head>
<body>
<h3>Adicionar produto</h3>
<form action="php/treatproducts.php" method="POST">

	EAN: <input type="text" name="ean"> <br>

	Design: <input type="text" name="design"> <br>

	Fornecedor Primario: <select id="forn-prim" name="forn_prim"><?php
		foreach ($forns as $row) {
			echo "<option>$row[0]</option>\n";
		}
		?>
		<option value="new">New</option>
	</select><br>

	<div id="novo-forn-prim" style="display: none">
		NIF: <input type="text" name="nif_fornecedor_primario"> <br>
		Nome: <input type="text" name="nome_fornecedor_primario"> <br>
	</div>

	Fornecedor Secundario: <select id="forn-sec" name="forn_sec"><?php
		foreach ($forns as $row) {
			echo "<option>$row[0]</option>\n";
		}
		?>
		<option value="new">New</option>
	</select><br>

	<div id="novo-forn-sec" style="display: none">
		NIF: <input type="text" name="nif_fornecedor_secundario"> <br>
		Nome: <input type="text" name="nome_fornecedor_secundario"> <br>
	</div>

	Categoria: <select name="categoria"><?php
		while ($row = $cats->fetch()) {
			echo "<option>$row[0]</option>\n";
		}
		?></select><br>

	<input type="submit">
</form>

<h3>Remover produto</h3>
<form action="php/treatproducts.php" method="POST">
	Escreva o EAN do produto:
	<select name="removeean">
		<?php
		while ($row = $eans->fetch()) {
			echo "<option>$row[0]</option>\n";
		}
		?>
	</select> <br>
	<input type="submit" value="Submit">
</form>

<script type="text/javascript">
	var forn_prim = document.getElementById('forn-prim');
    var novo_forn_prim = document.getElementById('novo-forn-prim');

    forn_prim.addEventListener('change', function() {
        if(forn_prim.options[forn_prim.selectedIndex].value !== 'new') {
            novo_forn_prim.style.display = 'none';
        } else {
            novo_forn_prim.style.display = 'inline';
        }
    });

    var forn_sec = document.getElementById('forn-sec');
    var novo_forn_sec = document.getElementById('novo-forn-sec');

    forn_sec.addEventListener('change', function() {
        if(forn_sec.options[forn_sec.selectedIndex].value !== 'new') {
            novo_forn_sec.style.display = 'none';
        } else {
            novo_forn_sec.style.display = 'inline';
        }
    });
</script>

<a href="index.html">Index</a>

</body>
</html>
