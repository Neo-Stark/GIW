import "./App.css";
import React from "react";
import { Button } from "@material-ui/core";
import isElectron from "is-electron";
import Alert from "@material-ui/lab/Alert";

class SearchBar extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      search: props.search,
    };
  }

  handleChange = (event) => {
    this.setState({ search: event.target.value });
  };

  render() {
    return (
      <div className="SearchBar">
        <form>
          <input
            type="text"
            onChange={this.handleChange}
            value={this.state.search}
          />
          <Button
            variant="contained"
            color="primary"
            type="submit"
            className="btn-grad"
            onClick={(ev) => this.props.onClick(this.state.search, ev)}
          >
            Buscar
          </Button>
        </form>
      </div>
    );
  }
}
const Results = (props) => {
  const openFile = (file) => window.ipcRenderer.send("open", file);
  return (
    <ol>
      {props.paths.map((file, index) => {
        let name = file.split("/").pop();
        return (
          <li key={index}>
            <Button
              variant="contained"
              className="btn-grad"
              onClick={() => openFile(file)}
            >
              {name}
            </Button>
          </li>
        );
      })}
    </ol>
  );
};

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      search: "",
      paths: [],
      notFound: false,
    };
  }

  handleClick = (search, event) => {
    event.preventDefault();
    if (isElectron()) {
      let result = window.ipcRenderer.sendSync("buscar", search);
      let paths = result.split("\n");
      paths = paths
        .filter((path) => path.indexOf("Doc:") !== -1)
        .map((path) => path.replace("Doc: ", ""));
      this.setState({
        paths: paths,
        search: search,
        notFound: !paths.lenght > 0,
      });
    }
  };

  render() {
    const searchBar = (
      <SearchBar
        search={this.state.search}
        onClick={(search, ev) => this.handleClick(search, ev)}
      />
    );
    if (!this.state.paths.length) {
      return (
        <div className="Search">
          {searchBar}
          {this.state.notFound && (
            <Alert
              severity="error"
              onClose={() => {
                this.setState({ notFound: false });
              }}
            >
              No se ha encontrado ningún resultado
            </Alert>
          )}
        </div>
      );
    } else {
      return (
        <div className="Result">
          <div className="top-bar">{searchBar}</div>
          <Results paths={this.state.paths} />
        </div>
      );
    }
  }
}

export default App;
