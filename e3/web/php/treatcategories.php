<?php
try {
    require 'connection.php';

    $catinsert = $_POST["categoryins"];
    $catremove = $_POST["categoryremove"];
    $subcatinsert = $_POST["categoryins1"];
    $supercat1 = $_POST["categoryins2"];
    $subcatremove = $_POST["subcategoryremove1"];
    $supercat2 = $_POST["subcategoryremove2"];

    if ($catinsert != "") {
        $stmt1 = $db->prepare('INSERT INTO categoria VALUES (?);');
        $stmt2 = $db->prepare('INSERT INTO categoria_simples VALUES(?);');

        $db->beginTransaction();
        $stmt1->execute([$catinsert]);
        $stmt2->execute([$catinsert]);
        $db->commit();
    }

    if ($catremove != "") {
        $stmt = $db->prepare("DELETE FROM categoria WHERE nome=?;");
        $db->beginTransaction();
        $stmt->execute([$catremove]);
        $db->commit();
    }

    if ($subcatinsert != "" and $supercat1 != "") {
        $stmt = $db->prepare("INSERT INTO constituida VALUES (:super, :sub);");
        $db->beginTransaction();
        $stmt->execute(['super' => $supercat1, 'sub' => $subcatinsert]);
        $db->commit();
    }

    if ($subcatremove != "" and $supercat2 != "") {
        $stmt1 = $db->prepare('DELETE FROM constituida WHERE super_categoria AND categoria = (:super, :sub);');
        $stmt2 = $db->prepare('DELETE FROM categoria_simples WHERE categoria=?');
        $stmt3 = $db->prepare('DELETE FROM categoria WHERE nome=?');
        $db->beginTransaction();
        $stmt1->execute(['super' => $supercat2, 'sub' => $subcatremove]);
        $stmt2->execute([$subcatremove]);
        $stmt3->execute([$subcatremove]);
        $db->commit();
    }


    $db = null;
} catch (PDOException $e) {
    echo("<p>ERROR: {$e->getMessage()}</p>");
}
?>
