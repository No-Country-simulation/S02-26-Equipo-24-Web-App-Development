import { create } from 'zustand'

type SurgeryState = {
    instrumentPosition: { x: number; y: number; z: number }
    lastEvent: string | null
    setInstrumentPosition: (pos: any) => void
    setEvent: (event: string) => void
}

export const useSurgeryStore = create<SurgeryState>((set) => ({
    instrumentPosition: { x: 0, y: 0, z: 0 },
    lastEvent: null,
    setInstrumentPosition: (pos) => set({ instrumentPosition: pos }),
    setEvent: (event) => set({ lastEvent: event }),
}))
