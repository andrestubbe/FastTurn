> [!WARNING]
> **🚧 WIP — Active Semantic Turn-Taking & Endpointing Pipeline Calibration in Progress.**

# FastTurn 0.1.0 [ALPHA] — Adaptive Semantic Endpointing & Turn-Taking Engine for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastTurn/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Cross--Platform-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastTurn)

---

**⚡ Adaptive semantic endpointing, four-state conversational machine, and sub-millisecond Barge-In controller for the FastJava voice ecosystem.**

**FastTurn** bridges acoustic voice activity detection (**[FastVAD](https://github.com/andrestubbe/FastVAD)**) and streaming speech recognition (**[FastSTT](https://github.com/andrestubbe/FastSTT)**). It solves the fundamental voice-agent dilemma ("when to start talking") by replacing rigid silence timers with dynamic semantic heuristics that give users time to think during mid-sentence pauses while answering finished sentences in under 250 milliseconds.

Watch Demo (YouTube) | Watch JMH Benchmark (YouTube)

---

## Quick Start

```java
import fastturn.FastTurn;
import fastturn.events.FastTurnEvents;

public class Example {
    public static void main(String[] args) {
        // 1. Initialize conversational turn engine
        FastTurn turn = new FastTurn(new FastTurnEvents() {
            @Override
            public void onTurnComplete(String finalTranscript) {
                // User finished speaking -> Dispatch to LLM
                System.out.println(">>> TURN COMPLETE: " + finalTranscript);
            }

            @Override
            public void onBargeIn() {
                // User interrupted agent reply -> Stop FastTTS immediately
                System.out.println("<<< BARGE-IN: Halt active TTS audio <<<");
            }
        });

        // 2. Feed streaming partial transcripts and VAD ticks
        turn.onPartialTranscript("Ich möchte gerne einen Tisch reservieren für");
        turn.onVadSpeechStart();
        turn.onVadSpeechEnd(300); // 300ms pause -> State stays COUNTING_SILENCE (Patient limit: 1200ms)
    }
}
```

---

## Table of Contents

- [Why FastTurn?](#why-fastturn)
- [Quick Start](#quick-start)
- [Features](#features)
- [Performance Benchmarks](#performance-benchmarks)
- [API Quick Reference](#api-quick-reference)
- [Technical Examples & Hero Demos](#technical-examples--hero-demos)
- [Installation](#installation)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

---

## Why FastTurn?

Standard voice agents rely solely on acoustic silence duration to decide when the user has finished talking:

- **The 300ms Problem**: A short timer makes the agent responsive, but interrupts users during natural mid-thought pauses ("My number is... 4155...").
- **The 1,200ms Drag**: A long timer avoids interruptions, but forces 1.2 seconds of dead air onto every single exchange.
- **Silence Is a Weak Proxy**: Humans detect turn ends using grammar and meaning, not silence alone.

**FastTurn** solves this with **Adaptive Semantic Endpointing**:

- **Dynamic Hesitation Scaling**: Automatically scales silence thresholds from **250 ms** (finished sentences / confirmations) up to **1,200 ms** (connectors like *und*, *oder*, *weil*, *äh*, *hmm*).
- **Sub-Millisecond Four-State Machine**: Manages transitions between `LISTENING`, `USER_SPEAKING`, `COUNTING_SILENCE`, and `AGENT_REPLYING` in < 1 µs.
- **Zero-Allocation Hot Path**: Operates with zero GC churn during continuous 24/7 microphone event streams.

---

## Features

- **🔄 Four-State Conversational Engine**: Deterministic tracking across Listening, Speaking, Counting Silence, and Replying states.
- **🧠 Adaptive Semantic Endpointing**: Dynamic pause limits (250 ms vs 800 ms vs 1200 ms) based on sentence completeness.
- **🛑 Sub-Millisecond Barge-In**: Emits instant `onBargeIn` callbacks to interrupt active **[FastTTS](https://github.com/andrestubbe/FastTTS)** playback.
- **⚡ Extreme Throughput**: Capable of evaluating over 33,000,000 full turn-taking cycles per second on the JVM.
- **📊 FastANSI 120-Column HUD**: Telemetry formatting with detailed state transition trees and pause metrics.

---

## Performance Benchmarks

FastTurn is rigorously profiled using **JMH** to guarantee zero overhead.

| Metric / Operation Type | Score (ops/ms) | Ops per Second |
|---|---|---|
| **Semantic Completeness Evaluation** | **~54,730 ops/ms** | **> 54.7 Million** |
| **Full Turn State-Machine Cycle** | **~33,578 ops/ms** | **> 33.5 Million** |
| **Hesitation Connector Parsing** | **~2,751 ops/ms** | **> 2.75 Million** |

*Measured on Windows 11 x64, Intel Core i5 (Surface Pro 8), JDK 21.0.12.1. Validations run entirely in-memory with zero heap object allocations in the hot path.*

---

## API Quick Reference

| Method | Description |
|---|---|
| `FastTurn(events)` | Creates a new conversational turn engine instance. |
| `onVadSpeechStart()` | Ingests acoustic speech start event from FastVAD. |
| `onVadSpeechEnd(deltaMs)` | Ingests acoustic silence delta duration in milliseconds. |
| `onPartialTranscript(text)` | Updates streaming partial transcription from FastSTT. |
| `getState()` | Returns current conversational `TurnState`. |

---

## Technical Examples & Hero Demos

| Case | Java Example | Launcher | Description |
|---|---|---|---|
| **Interactive 120-Column HUD Demo** | [Demo.java](src/main/java/fastturn/Demo.java) | `run-demo.bat` | Terminal demonstration of Patient Mode, Fast Response Mode, and Barge-In interrupts. |
| **JMH Microbenchmark Suite** | [FastTurnBenchmark.java](examples/Benchmark/src/main/java/fastturn/benchmark/FastTurnBenchmark.java) | `run-benchmark.bat` | Formal OpenJDK JMH throughput measurements across state transitions and heuristics. |

---

## Installation

### Option 1: Maven (Recommended)

Add the JitPack repository and the dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <!-- FastTurn Core -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastTurn</artifactId>
        <version>0.1.0</version>
    </dependency>

    <!-- FastVAD Acoustic Engine -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastVAD</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastTurn:0.1.0'
    implementation 'com.github.andrestubbe:FastVAD:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)
Download the latest JARs directly to add them to your classpath:

1. 📦 **[FastTurn-0.1.0.jar](https://github.com/andrestubbe/FastTurn/releases/download/0.1.0/FastTurn-0.1.0.jar)** (The Core Turn-Taking Engine)
2. 🎙️ **[FastVAD-0.1.0.jar](https://github.com/andrestubbe/FastVAD/releases/download/0.1.0/FastVAD-0.1.0.jar)** (The Acoustic VAD Substrate)
3. ⚙️ **[fastcore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/fastcore-0.1.0.jar)** (The Mandatory Runtime Substrate)

---

## Documentation

* **[REFERENCE.md](docs/REFERENCE.md)**: Full API descriptions, state transitions, and memory guarantees.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: The architectural rationale for adaptive semantic endpointing.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones, prosody pitch cues, and multi-language models.
* **[CHANGELOG.md](docs/CHANGELOG.md)**: Release history and version migration details.

---

## Platform Support

| Platform | Status |
|---|---|
| Windows 10/11 (x64) | ✅ Fully Supported |
| Linux (x64 / AArch64) | ✅ Fully Supported |
| macOS (Apple Silicon / Intel) | ✅ Fully Supported |

---

## License

MIT License — See [LICENSE](LICENSE) for details.

---

## Related Projects

Combine FastTurn with other FastJava voice accelerators:

* [**FastVAD**](https://github.com/andrestubbe/FastVAD) — Ultra-fast real-time voice activity detection (Silero-ONNX & WebRTC).
* [**FastSTT**](https://github.com/andrestubbe/FastSTT) — High-throughput streaming speech-to-text recognition.
* [**FastTTS**](https://github.com/andrestubbe/FastTTS) — Low-latency streaming text-to-speech synthesis.
* [**FastAIAgent**](https://github.com/andrestubbe/FastAIAgent) — Autonomous multi-agent orchestration.

---

**Part of the FastJava Ecosystem** — *Making the JVM faster.*