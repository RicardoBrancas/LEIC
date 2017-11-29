<html>

<head>
	<title>Results - Alinea D</title>
</head>

<body>
<?php

require 'connection.php';

$ean = $_POST['ean'];
$des = $_POST['newdesign'];

if (!empty($des)) {
	$stmt = $db->prepare("UPDATE produto SET design=:des WHERE ean=:ean");
	$stmt->execute(['des' => $des, 'ean' => $ean]);

	echo "Designacao do produto $ean alterada para $des com sucesso!";
	echo "<pre>OK! ran:\nUPDATE produto SET design='$des' WHERE ean=$ean;</pre>";
}
else {
	echo 'A nova designacao nao e valida.';
}
?>
<a href="../d.php">Back</a>

</body>
</html>