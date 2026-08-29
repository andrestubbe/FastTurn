# FastTurn Reference & API Specification

## 1. Core Vocabulary

*   **Turn-Taking**: The coordination of conversational speaking rights between human user and AI voice agent.
*   **Adaptive Semantic Endpointing**: Dynamically varying silence timers between 250ms and 1200ms depending on sentence completeness.
*   **Patient Mode**: Extending the silence limit to 1200ms when sentence-connecting words (*und*, *oder*, *weil*, *äh*) indicate active user thinking.
*   **Fast Response Mode**: Shortening the silence limit to 250ms on punctuation marks (*.*, *!*, *?*) and direct confirmations (*ja*, *nein*, *ok*).
*   **Barge-In**: Immediately canceling active TTS audio when the user begins speaking while the agent is in `AGENT_REPLYING` state.

## 2. State Machine Transitions

```
[LISTENING] ──(SpeechStart)──► [USER_SPEAKING] ──(SpeechEnd)──► [COUNTING_SILENCE]
     ▲                                                                │
     │                                                     (Silence >= Limit)
     │                                                                ▼
[AGENT_REPLYING] ◄────────────────────────────────────────────────────┘
     │
 (SpeechStart / BargeIn) ──► [USER_SPEAKING]
```

---
**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*