<?php

// nested arrays assign

function i($n) {
    if ($n > 0) {
        $n --;
        $B = array(i($n), i($n));
        return $B;
    }
    return array(-1, -1);
}

function c($A) {
    $res = 1;
    if ($A[0] != -1) {
        $res += c($A[0]);
    }
    if ($A[1] != -1) {
        $res += c($A[1]);
    }
    return $res;
}

$tree = i(6);
$tree[0][0][0][0][0][0][0] = 1;
print($tree[0][0][0][0][0][0][0]);












?>
