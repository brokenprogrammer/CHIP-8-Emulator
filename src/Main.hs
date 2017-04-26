{-# LANGUAGE OverloadedStrings #-}
module Main where

import Control.Monad
import Foreign.C.Types
import SDL.Vect
import qualified SDL

screenWidth, screenHeight :: CInt
(screenWidth, screenHeight) = (640, 480)

main :: IO ()
main = do
  SDL.initialize [SDL.InitVideo]

  window <- SDL.createWindow "CHIP8-Emulator" SDL.defaultWindow { SDL.windowInitialSize = V2 screenWidth screenHeight }
  SDL.showWindow window

  screenSurface <- SDL.getWindowSurface window
  let black = V4 0 0 0 0

  let
    loop = do
      events <- SDL.pollEvents
      let quit = elem SDL.QuitEvent $ map SDL.eventPayload events

      SDL.surfaceFillRect screenSurface Nothing black
      SDL.updateWindowSurface window

      unless quit loop

  loop

  SDL.destroyWindow window
  SDL.quit
