<html>

<head>
	<title>Results - Alinea E</title>
</head>

<body>
<?php

require 'connection.php';

$cat_name = $_GET['category'];

$sql =
    "WITH RECURSIVE recurse(nome) AS (
        SELECT categoria from constituida WHERE super_categoria = ?
        UNION
        SELECT categoria AS nome
        FROM recurse
        JOIN constituida ON constituida.super_categoria = nome  
    )
    SELECT * FROM recurse;";

//$sql = "SELECT all_subcategories(?);";

try {
	$stmt = $db->prepare($sql);
	$stmt->execute([$cat_name]);

	if($stmt->rowCount() > 0) {
		?>

		<table>
			<th>Nome</th>

			<?php
			while($row = $stmt->fetch()) {
				echo "<tr><td>$row[0]</td></tr>";
			}
			?>

		</table>

	<?php } else {
		echo 'A categoria escolhida nao existe ou nao tem subcategorias.';
	}

} catch (PDOException $e) {
	echo("<p>Ocorreu um erro inesperado: {$e->getMessage()}</p>");
}

echo "<pre>OK! ran:\nWITH RECURSIVE recurse(nome) AS (
        SELECT categoria from constituida WHERE super_categoria = '$cat_name'
        UNION
        SELECT categoria AS nome
        FROM recurse
        JOIN constituida ON constituida.super_categoria = nome  
    )
    SELECT * FROM recurse;</pre>";
?>

<a href="../e.php">Back</a>

</body>
</html>

