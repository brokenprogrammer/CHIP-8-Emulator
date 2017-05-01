module Chip8.Memory where

import Data.Bits
import Data.Word

data Address = Register Register
             | Pc
             | Sp
             | Stack
             | Ram Word16
             deriving (Show)

data Register = V0 | V1 | V2 | V3
              | V4 | V5 | V6 | V7
              | V8 | V9 | VA | VB
              | VC | VD | VE | VF
              | DT | ST | I
              deriving (Enum, Show)
