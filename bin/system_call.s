
.section .text
.global _system_call

_system_call:
    stp x29, x30, [sp, #-16]!  // Save frame pointer and return address
    mov x29, sp                // Update frame pointer

    mov x8, x0                 // Move system call number to x8 (syscall number register)
    mov x0, x1                 // Move first argument to x0
    mov x1, x2                 // Move second argument to x1
    mov x2, x3                 // Move third argument to x2
    mov x3, x4                 // Move fourth argument to x3
    mov x4, x5                 // Move fifth argument to x4
    mov x5, x6                 // Move sixth argument to x5

    svc #0                     // Make the system call

    ldp x29, x30, [sp], #16    // Restore frame pointer and return address
    ret                        // Return to caller
