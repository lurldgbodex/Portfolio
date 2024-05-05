import './App.css'
import Bio from './components/bio/Bio'
import ContactForm from './components/contact/ContactForm'
import NavBar from './components/navbar/NavBar'
import Project from './components/project/Project'
import SideBar from './components/sidebar/SideBar'



function App() {
  
  return (
    <>
      <div className='app'>
        <SideBar />
        <div className='main-content'>
          <div className="conten-container">
            <Bio />
            <section className='project'>
              <h2 className='h2'>Projects</h2>
              <p className='project_p'>This is a collection of select projects that makes up my portfolio. You can click on the project for more details about the project.</p>
            </section>
            <section>
              <Project />
            </section>
          </div>
          </div>
        <NavBar />
      </div>
      <footer>
        <ContactForm />
      </footer>
    </>
  )
}

export default App
