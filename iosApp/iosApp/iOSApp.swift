/*import SwiftUI
import shared

class AppDelegate: NSObject, UIApplicationDelegate {

    func application(
      _ app: UIApplication,
      didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        // Initialize Gemini Live Bridge
        IOSGeminiLiveService.companion.bridge = iOSGeminiLiveHandler()
        return true
    }

    func application(
      _ app: UIApplication,
      open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
      var handled: Bool

      handled = GIDSignIn.sharedInstance.handle(url)
      if handled {
        return true
      }

      // Handle other custom URL types.

      // If not handled by this app, return false.
      return false
    }


}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}*/
import SwiftUI
import Firebase
import GoogleSignIn
import shared

class AppDelegate: NSObject, UIApplicationDelegate {

    func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {

        // Firebase initialization (REQUIRED)
        if FirebaseApp.app() == nil {
            FirebaseApp.configure()
        }

        // Gemini Live bridge (safe to keep)
        IOSGeminiLiveService.companion.bridge = iOSGeminiLiveHandler()

        return true
    }

    func application(
    _ app: UIApplication,
    open url: URL,
    options: [UIApplication.OpenURLOptionsKey: Any] = [:]
    ) -> Bool {

        // Google Sign-In handler
        if GIDSignIn.sharedInstance.handle(url) {
            return true
        }

        return false
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}