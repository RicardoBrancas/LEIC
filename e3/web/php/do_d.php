<html>

<head>
	<title>Results - Alinea D</title>
</head>

<body>
<?php

require 'connection.php';

$ean = $_POST['insert_ean'];
$design = $_POST['insert_design'];

if (!empty($design)) {
	$stmt = $db->prepare("UPDATE produto SET design=:des WHERE ean=:ean");
	$stmt->execute(['des' => $design, 'ean' => $ean]);

	echo "Designacao do produto $ean alterada para $design com sucesso!";
	echo "<pre>OK! ran:\nUPDATE produto SET design='$design' WHERE ean=$ean;</pre>";
}
else {
	echo 'A nova designacao nao e valida.';
}
?>
<a href="../d.php">Back</a>

</body>
</html>
