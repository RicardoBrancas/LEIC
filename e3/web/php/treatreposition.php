<?php

require 'connection.php';

$ean = $_POST['ean'];

$stmt = $db->prepare("SELECT * 
											FROM reposicao NATURAL JOIN evento_reposicao
											WHERE ean=:ean");
$stmt->execute(['ean' => $ean]);

if($stmt->rowCount() > 0) {
?>

<table>
	<?php
	echo("<tr><th>EAN</th><th>Operador</th><th>Instante</th><th>Unidades Repostas</th></tr>");

	while($row = $stmt->fetch())
		echo "<tr><td>$row['ean']</td><td>$row['operador']</td><td>$row['instante']</td><td>$row['unidades']</td></tr>";

?>
</table>
<?php
}
