import SwiftUI
import UIKit
import Collegue

@main
struct CollegueApp: App {
    var body: some Scene {
        WindowGroup { SchoolView().ignoresSafeArea(.all) }
    }
}

private struct SchoolView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }
    func updateUIViewController(_ controller: UIViewController, context: Context) { }
}
