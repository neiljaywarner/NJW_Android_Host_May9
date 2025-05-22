# Maestro UI Testing

## What is Maestro?

Maestro is a simple, powerful UI testing framework for mobile apps. It allows you to write readable
and maintainable tests in YAML format without needing to understand complex programming concepts or
frameworks.

Key benefits of Maestro:

- **Simple**: Tests are written in YAML, making them easy to read and write
- **Fast**: Tests run quickly and reliably
- **Cross-platform**: Works with both Android and iOS apps
- **No coding required**: Non-developers can write and maintain tests
- **Visual feedback**: Captures screenshots during test execution

## Installation

### Installing Maestro CLI

1. **macOS/Linux**:
   ```bash
   curl -Ls "https://get.maestro.mobile.dev" | bash
   ```

2. **Windows**:
   ```bash
   powershell -Command "& $([scriptblock]::Create((New-Object Net.WebClient).DownloadString('https://get.maestro.mobile.dev/win')))"
   ```

3. Verify the installation:
   ```bash
   maestro --version
   ```

## Writing Your First Test

Tests in Maestro are written as YAML files. Here's a simple example:

```yaml
appId: com.neiljaywarner.androidhostappimagemay9
---
- launchApp
- takeScreenshot: initial_screen
- tapOn: "Login"
- inputText: "username"
- tapOn: "Password"
- inputText: "password123"
- tapOn: "Submit"
- assertVisible: "Welcome"
- takeScreenshot: success_screen
```

## Running Tests

To run a Maestro test:

```bash
maestro test path/to/your/test.yaml
```

For example, to run our basic flow test:

```bash
maestro test maestro_tests/basic_flow.yaml
```

## Screenshots and Test Results

Maestro stores test results including screenshots in a timestamped directory under
`~/.maestro/tests/`. Each test run creates a folder with a name format of `YYYY-MM-DD_HHMMSS`.

For example:

```
/Users/username/.maestro/tests/2025-05-21_185930/
```

The screenshots themselves are embedded in the HTML report that Maestro generates. To view the
report, open the `ai-report-basic_flow.html` file in your browser.

When running the test, you can also specify a custom output directory:

```bash
maestro test path/to/your/test.yaml --output custom/output/dir
```

If you want full debug output:

```bash
maestro test path/to/your/test.yaml --debug-output custom/output/dir
```

## Common Commands

Here are some common Maestro commands:

- `launchApp`: Launches the app
- `tapOn`: Taps on a UI element (by text, ID, etc.)
- `inputText`: Types text into the focused field
- `assertVisible`: Checks if an element is visible
- `takeScreenshot`: Takes a screenshot and saves it
- `scroll`: Scrolls in a specified direction
- `back`: Simulates pressing the back button
- `swipe`: Performs a swipe gesture

## Best Practices

1. **Keep tests small and focused**: Each test should verify one specific flow
2. **Use readable names**: Name your tests and screenshots descriptively
3. **Add comments**: Document complex flows with comments
4. **Visual verification**: Use screenshots to verify important states
5. **Stable selectors**: Use IDs when possible for more stable tests

## Troubleshooting

If your tests are failing, check:

1. Is the app installed on the device?
2. Is the app ID correct?
3. Are the UI elements you're targeting actually present?
4. Are you waiting long enough for screens to load?
5. **Important**: Make sure to use `clearState` to close the app before launching it to ensure a
   fresh state

## Resources

- [Official Maestro Documentation](https://maestro.mobile.dev/)
- [Maestro GitHub](https://github.com/mobile-dev-inc/maestro)
- [Maestro Command Reference](https://maestro.mobile.dev/api-reference/commands)

## Example Tests

Check out the example tests in this directory to see how Maestro works with our app:

- `basic_flow.yaml`: A simple test that launches the app and takes a screenshot