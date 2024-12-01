.class public Main
.super java/lang/Object

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 2
    .limit locals 2

    ; Initialize i to 5
    iconst_5
    istore_1

    ; Start the loop
    Loop:
        ; Print current value of i
        getstatic java/lang/System/out Ljava/io/PrintStream;
        iload_1
        invokevirtual java/io/PrintStream/println(I)V

        ; Decrement i
        iload_1
        iconst_1
        isub
        istore_1

        ; Compare i with 1
        iload_1
        iconst_1
        if_icmpge Loop

    return
.end method
