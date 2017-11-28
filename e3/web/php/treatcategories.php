<?php
try {
    require 'connection.php';

    $catinsert = $_POST["categoryins"];
    $catremove = $_POST["categoryremove"];
    $subcatinsert = $_POST["categoryins1"];
    $supercat1 = $_POST["category"];
    $subcatremove = $_POST["subcategoryremove1"];
    $supercat2 = $_POST["category1"];

    if ($catinsert != "") {
        $stmt1 = $db->prepare('INSERT INTO categoria VALUES (?);');
        $stmt2 = $db->prepare('INSERT INTO categoria_simples VALUES(?);');

        $db->beginTransaction();
        $stmt1->execute([$catinsert]);
        $stmt2->execute([$catinsert]);
        $db->commit();
    }

    if ($catremove != "") {
        $stmt1 = $db->prepare("DELETE FROM categoria_simples WHERE nome=?;");
        $stmt2 = $db->prepare("DELETE FROM categoria WHERE nome=?;");

        $db->beginTransaction();
        $stmt1->execute([$catremove]);
        $stmt2->execute([$catremove]);
        $db->commit();
    }

    if ($subcatinsert != "" and $supercat1 != "") {
        $stmt1 = $db->prepare("INSERT INTO categoria VALUES (:cat);");
        $stmt2 = $db->prepare("INSERT INTO categoria_simples VALUES (:sub);");
        $stmt3 = $db->prepare("INSERT INTO constituida VALUES (:super, :sub);");
        $db->beginTransaction();
        $stmt1->execute(['cat' => $subcatinsert]);
        $stmt2->execute(['sub' => $subcatinsert]);
        $stmt3->execute(['super' => $supercat1, 'sub' => $subcatinsert]);
        $db->commit();
    }

    if ($subcatremove != "" and $supercat2 != "") {
        $stmt1 = $db->prepare('DELETE FROM constituida WHERE super_categoria = :super AND categoria = :sub;');
        $stmt2 = $db->prepare('DELETE FROM categoria_simples WHERE nome=?');
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
