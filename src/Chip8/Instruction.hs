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
                 | SETB    Register Word8         -- Set Register value to an Word8.
                 | ADDB    Register Word8         -- Add Word8 to Register value.
                 | ASSIGNR Register Register      -- Set second Register value to first Register.
                 | BITOR   Register Register      -- Set first Register to bitwise OR operation on the Registers.
                 | BITAND  Register Register      -- Set first Register to bitwise AND operation on the Registers.
                 | BITXOR  Register Register      -- Set first Register to bitwise XOR operation on the Registers.
                 | ADDR    Register Register      -- Second Register is added to first Register.
                 | SUBR    Register Register      -- Second Register is subtracted from first Register.
                 | SHIFTR  Register               -- Divide Register value by 2 through shifting the Register right by one.
                 | SUBFRST Register Register      -- First Register is subtracted from second Register, stored in first Register.
                 | SHIFTL  Register               -- Multiply Register value by 2 through shifting the Register left by one.
                 | SKIPNER Register Register      -- Skip next instruction if registers are not equal.
                 | SETIADR Address                -- Set register I value to Address.
                 | LONGJP  Address                -- Jump to location Address
                 | RND     Register Word8         -- Set register value to bitwise AND of random byte and Word8.
                 | DRAW    Register Register Word8-- Display sprite at memory location I at (Register, Register).
                 | SKIPPR  Register               -- Skip next instruction if key in Register is pressed.
                 | SKIPNP  Register               -- Skip next instruction if key in Register is not pressed.
                 | SETRTIM Register               -- Set Register to the value in delay timer.
                 | WAITKEY Register               -- Wait for a key press, store the value of the key in Register.
                 | SETTIMR Register               -- Set delay time to Register.
                 | SETSNDT Register               -- Set sound timer to Register.
                 | ADDI    Register               -- Add Register to I value.
                 | ISPRITE Register               -- Set I to the location of sprite for digit stored in Register.
                 | STRBCD  Register               -- Store BCD representation of Register in memory locations I, I+1, I+2.
                 | STOREV  Register               -- Store registers V0 through Register in memory starting at location I.
                 | FILLV   Register               -- Read registers V0 through Register from memory starting at location I.
                 deriving (Show)

-- Decodes an opcode and gives back its correspoding instruction.
decodeInstruction :: Word16 -> Instruction
