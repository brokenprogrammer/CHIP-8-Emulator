module Chip8.Instruction where

import Data.Bits
import Data.Word

data Instruction = SYS     Address                -- Jump to a machine code routine at Address
                 | CLEAR                          -- Clears the screen.
                 | RETURN                         -- Returns from a subroutine.
                 | JUMP    Address                -- Jumps to address NNN.
                 | CALL    Address                -- Calls subroutine at NNN.
                 | SKIPEQ  Register Word8         -- Skip next instruction if the Register value equals Word8.
                 | SKIPNEQ Register Word8         -- Skip next instruction if Register value not equals Word8.
                 | SKIPREQ Register Register      -- Skip next instruction if registers contain same value.
                 | LDB     Register Word8         -- Set Register value to Word8
                 | ADDB    Register Word8         -- Add Word8 to Register value
                 | LDR     Register Register      -- Copy second Register value to first Register
                 | OR      Register Register      -- Set first Register value to bitwise OR of both register values
                 | AND     Register Register      -- Set first Register value to bitwise AND of both register values
                 | XOR     Register Register      -- Set first Register value to bitwise XOR of both register values
                 | ADDR    Register Register      -- Second Register value is added to first Register value
                 | SUB     Register Register      -- Second Register value is subtracted from first Register value
                 | SHR     Register               -- Divide Register value by 2
                 | SUBN    Register Register      -- First Register value is subtracted from second Register value, result stored in first Register value
                 | SHL     Register               -- Multiply Register value by 2
                 | SNER    Register Register      -- Skip next instruction if registers contain different values
                 | LDI     Address                -- Set register I value to Address
                 | LONGJP  Address                -- Jump to location Address
                 | RND     Register Word8         -- Set register value to bitwise AND of random byte and Word8
                 | DRW     Register Register Word8-- Display sprite at memory location I at (Register, Register)
                 | SKP     Register               -- Skip next instruction if key with the value of Register is pressed
                 | SKNP    Register               -- Skip next instruction if key with the value of Register is not pressed
                 | LDRDT   Register               -- Set Register value to delay timer value
                 | LDK     Register               -- Wait for a key press, store the value of the key in Register
                 | LDDTR   Register               -- Set delay time to Register value
                 | LDST    Register               -- Set sound timer to Register value
                 | ADDI    Register               -- Add Register value to I register value
                 | LDF     Register               -- Set I to the location of sprite for digit stored in Register
                 | LDBCD   Register               -- Store BCD representation of Register in memory locations I, I+1, I+2
                 | LDIR    Register               -- Store registers V0 through Register in memory starting at location I
                 | LDRI    Register               -- Read registers V0 through Register from memory starting at location I
                 deriving (Show)

-- Decodes an opcode and gives back its correspoding instruction.
decodeInstruction :: Word16 -> Instruction
